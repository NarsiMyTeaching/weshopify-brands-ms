package com.weshopify.platform.features.brands.resource;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.weshopify.platform.features.brands.bean.BrandBean;
import com.weshopify.platform.features.brands.domain.Brand;
import com.weshopify.platform.features.brands.exceptions.BrandNotFoundException;
import com.weshopify.platform.features.brands.service.BrandService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.extern.slf4j.Slf4j;

//@Controller
@RestController
@Slf4j
public class BrandResource {
	@Autowired private BrandService brandService;	
	
	@GetMapping("/brands")
	public ResponseEntity<List<Brand>> listFirstPage() {
		List<Brand> brandsList = brandService.listAll();
		return ResponseEntity.status(HttpStatus.OK).body(brandsList);
	}
	
	@GetMapping("/brands/page/{pageNum}/{noOfRecPerPage}")
	public ResponseEntity<List<Brand>> listByPage(@PathVariable(name = "pageNum") int pageNum,@PathVariable(name = "noOfRecPerPage") int noOfRecPerPage) {
		List<Brand> brandsList = brandService.listByPage(pageNum, noOfRecPerPage);
		return ResponseEntity.status(HttpStatus.OK).body(brandsList);	
	}
	
	@PostMapping(value = "/brands/save")
	public ResponseEntity<BrandBean> saveBrand(@RequestBody BrandBean brandBean) throws IOException {
		if (!brandBean.getFileImage().isEmpty()) {
			log.info("Brand Bean data is:\t"+brandBean.getName());
			File file = new File(brandBean.getFileImage());
			String fileName = StringUtils.cleanPath(file.getName());
			brandBean.setLogo(fileName);
			
			brandBean = brandService.save(brandBean);
			String uploadDir = "brand-logos/" + brandBean.getId();
			
			//AmazonS3Util.removeFolder(uploadDir);
			//AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
		} else {
			brandBean = brandService.save(brandBean);
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(brandBean);
	}
	
	@GetMapping("/brands/{id}")
	public ResponseEntity<BrandBean> editBrand(@PathVariable(name = "id") Integer id) {
		BrandBean brand = null;
		try {
			brand = brandService.get(id);
						
		} catch (BrandNotFoundException ex) {
			
		}
		if(brand != null) {
			return ResponseEntity.status(HttpStatus.OK).body(brand);
		}else {
			//build the json object with the exception message  or throw an exception
			return ResponseEntity.status(HttpStatus.OK).body(brand);
		}
	}
	
	@GetMapping("/brands/delete/{id}")
	public ResponseEntity<List<Brand>> deleteBrand(@PathVariable(name = "id") Integer id) {
		List<Brand> brandsList = null;
		try {
			brandsList = brandService.delete(id);
			
		} catch (BrandNotFoundException ex) {
			
		}
		if(brandsList != null) {
			return ResponseEntity.status(HttpStatus.OK).body(brandsList);
		}else {
			//build the json object with the exception message  or throw an exception
			return ResponseEntity.status(HttpStatus.OK).body(brandsList);
		}
	}	
}
