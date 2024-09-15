package com.example.apispring.repositorys;
import com.example.apispring.models.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

}
