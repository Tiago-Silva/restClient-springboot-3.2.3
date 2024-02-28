package br.com.demorestclient;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@SpringBootApplication
public class DemoRestclientApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoRestclientApplication.class, args);
    }

    @Bean
    ApplicationRunner runner(CrudService crudService) {
        return args -> {
            var magra = new Cat("Miau", "Black", 3.5);
            var gorda = new Cat("Gorginha", "Yelow", 6.5);

            final var id = "65df40e972109f03e8c7f56a";
            System.out.println(crudService.getCat(id));

            gorda = gorda.withWeight(9.5);
            crudService.updateCat(id, gorda);
            System.out.println(crudService.getCat(id));
            crudService.deleteCat(id);
            System.out.println(crudService.listCats());
        };
    }

    @Bean
    RestClient crudClient() {
        return RestClient.create("https://crudcrud.com/api/4dfa2aebfe614e2ebd839c4b8f7ede51");
    }
}
@Service
class CrudService {
    private final RestClient restClient;
    public CrudService(RestClient restClient) {
        this.restClient = restClient;
    }

    public Cat createCat(Cat cat) {
        return restClient
            .post()
            .uri("/cats")
            .body(cat)
            .retrieve()
            .body(Cat.class);

    }

    public Cat getCat(String id) {
        return restClient
            .get()
            .uri("/cats/{id}", id)
            .retrieve()
            .body(Cat.class);
    }

    public void updateCat(String id, Cat cat) {
        restClient
            .put()
            .uri("/cats/{id}", id)
            .body(cat)
            .retrieve()
            .toBodilessEntity();
    }

    public void deleteCat(String id) {
        restClient
            .delete()
            .uri("/cats/{id}", id)
            .retrieve()
            .toBodilessEntity();
    }

    public List<Cat> listCats() {
        return restClient
            .get()
            .uri("/cats")
            .retrieve()
            .body(new ParameterizedTypeReference<>() {
            });
    }
}

record Cat(String name, String color, Double weight) {

    public Cat withWeight(Double newWeight) {
        return new Cat(name, color, newWeight);
    }
}
