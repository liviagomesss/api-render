package com.example.apispring.controllers;

import com.example.apispring.models.Produto;
import com.example.apispring.repositorys.ProdutoRepository;
import com.example.apispring.services.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController // o rest indica que o retorno vai ser um json, o controller vai encontrar.
@RequestMapping("/api/produtos")
public class ProdutoController {
    private  final ProdutoRepository produtoRepository; //o final indica que o valor não pode ser mudado.
    private final ProdutoService produtoService;

    @Autowired //ele faz a injeção de dependência no programa. Ele não é necessário quando criamos o programa.
    public ProdutoController(ProdutoRepository produtoRepository, ProdutoService produtoService){
        this.produtoRepository = produtoRepository;
        this.produtoService = produtoService;
    }

//    @GetMapping("/selecionar")
//    public List<Produto> listarProdutos(){
//        return produtoRepository.findAll();
//    }// o findAll é um método list que usa a classe como parâmetro. Pode ser considerado um método padrão do pacote Java.

    @GetMapping("/selecionar")
    @Operation(summary = "Lista de todos os produtos", description = "Retorna uma lista com todos os produtos disponiveis")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Lista de produtos retornada com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Produto.class))

                    ),

                    @ApiResponse(
                            responseCode = "500", description = "Erro interno do servidor",
                            content = @Content
                    )
            }

    )
    public List<Produto> listarProdutos(){
        return produtoService.buscarTodosProdutos();
    }


    @PostMapping("/inserir")
    @Operation(summary = "Inserir um produto", description = "Inseri um porduto")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Lista de produtos retornada com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Produto.class))

                    ),

                    @ApiResponse(
                            responseCode = "500", description = "Erro interno do servidor",
                            content = @Content
                    )
            }

    )
    public ResponseEntity<String> inserirProduto(@Valid @RequestBody Produto produto, BindingResult resultado){
        if (resultado.hasErrors()){
            Map<String, String> erros = new HashMap<>();
            for (FieldError erro : resultado.getFieldErrors()) {
                // Coloque o nome do campo e a mensagem de erro no mapa
                erros.put(erro.getField(), erro.getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros.toString());
        }else {
            produtoService.salvarProduto(produto);
            return ResponseEntity.ok("Produto inserido com sucesso");
        }
    }

    @DeleteMapping("/excluir/{id}")
    @Operation(summary = "Exclui um produto", description = "Exclui um produto")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Removido com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Produto.class))

                    ),

                    @ApiResponse(
                            responseCode = "500", description = "Erro interno do servidor",
                            content = @Content
                    )
            }

    )
    public ResponseEntity<String> excluirProduto(@Parameter(description = "ID do produto a ser excluido") @PathVariable Long id){ //O pathVariable serve para inserir uma variável na url

        if (produtoService.buscarPorId(id) != null) {
            produtoService.excluirProduto(id);
            return ResponseEntity.ok("Produto excluído com sucesso");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
        }

    }

    @PutMapping("/atualizar/{id}")
    @Operation(summary = "Atuliza um produto", description = "Atualiza o produto")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Lista de produtos retornada com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Produto.class))

                    ),

                    @ApiResponse(
                            responseCode = "500", description = "Erro interno do servidor",
                            content = @Content
                    )
            }

    )
    public ResponseEntity<String> atualizarProduto(@PathVariable long id,
                                                   @Valid @RequestBody Produto produtoAtualizado, BindingResult resultado){

        if (resultado.hasErrors()){
            Map<String, String> erros = new HashMap<>();
            for (FieldError erro : resultado.getFieldErrors()) {
                // Coloque o nome do campo e a mensagem de erro no mapa
                erros.put(erro.getField(), erro.getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros.toString());
        }else {
            Produto produtoExistence = produtoService.buscarPorId(id);
            Produto produto = produtoExistence;
            produto.setNome(produtoAtualizado.getNome());
            produto.setDescricao(produtoAtualizado.getDescricao());
            produto.setPreco(produtoAtualizado.getPreco());
            produto.setQuantidadeEstoque(produtoAtualizado.getQuantidadeEstoque());
            produtoService.salvarProduto(produto);
            return ResponseEntity.ok("Produto atualizado com sucesso");
        }
    }


    @PatchMapping ("/atualizarParcial/{id}")
    public ResponseEntity<String> atualizarParcial(@PathVariable Long id,
                                                   @RequestBody Map<String, Object> mudancas){

        Produto produtoExistente = produtoService.buscarPorId(id);
        if (produtoExistente != null){
            Produto produto = produtoExistente;
            if (mudancas.containsKey("nome")){
                produto.setNome(String.valueOf(mudancas.get("nome")));
            }
            if (mudancas.containsKey("descricao")){
                produto.setDescricao(String.valueOf(mudancas.get("descricao")));
            }
            if (mudancas.containsKey("preco")){
                produto.setPreco(Double.valueOf(mudancas.get("preco").toString()));
            }
            if (mudancas.containsKey("quantidadeestoque")){
                produto.setQuantidadeEstoque(Integer.valueOf(mudancas.get("quantidadeestoque").toString()));
            }

            produtoService.salvarProduto(produto);

           return ResponseEntity.ok("Alterado com sucesso");
        }
        return ResponseEntity.notFound().build();
    }
}




