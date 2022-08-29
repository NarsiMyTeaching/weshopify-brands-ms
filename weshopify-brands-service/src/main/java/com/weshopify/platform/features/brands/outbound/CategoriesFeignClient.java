package com.weshopify.platform.features.brands.outbound;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(url = "${service.category.baseUri}",name = "weshopify-categories-service")
//@FeignClient(name = "WESHOPIFY-CATEGORIES-SERVICE")
@FeignClient(name = "weshopify-categories-service")
public interface CategoriesFeignClient {

	 @GetMapping(value ="/categories/{id}")
	public ResponseEntity<String> findCategoryById(@PathVariable("id") int id);
}
