package com.weshopify.platform.features.brands.inbound.listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.weshopify.platform.features.brands.domain.Brand;
import com.weshopify.platform.features.brands.repo.BrandRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CategoryUpdatesListener {

	@Autowired
	private BrandRepository repo;
	
	
	
	@RabbitListener(queues = {"${spring.rabbitmq.qname}"})
	public void recieveCategoryUpdates(String categoryEventJsonData) {
		log.info("category event data we got is:\t"+categoryEventJsonData);
		JSONObject categoryEventObj = new JSONObject(categoryEventJsonData);
		int eventCatId = categoryEventObj.getInt("catId");
		
		Iterator<Brand> brandsIt = repo.findAll().iterator();
		
		List<Brand> brandsList = new ArrayList<>();
		brandsIt.forEachRemaining(brandsList::add);
		
		brandsList.stream().forEach(brand->{
			String brandsCats = brand.getCategories();
			JSONArray brandsArray = new JSONArray(brandsCats);
			for(int i=0;i<brandsArray.length();i++) {
				//boolean isObjectNotThere = brandsArray.isNull(i);
				//if(!isObjectNotThere) {
					JSONObject dbCatJson = brandsArray.getJSONObject(i);
					int dbCatId = dbCatJson.getInt("catId");
					if(dbCatId == eventCatId) {
						brandsArray.remove(i);
						brandsArray.put(categoryEventObj);
					}
				//}
				
				
			}
			String updatedCats = brandsArray.toString();
			brand.setCategories(updatedCats);
			repo.save(brand);
		});
		
		
		
	}
}
