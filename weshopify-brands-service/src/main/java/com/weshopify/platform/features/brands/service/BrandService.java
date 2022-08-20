package com.weshopify.platform.features.brands.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weshopify.platform.features.brands.bean.BrandBean;
import com.weshopify.platform.features.brands.bean.CategoryBean;
import com.weshopify.platform.features.brands.domain.Brand;
import com.weshopify.platform.features.brands.domain.Category;
import com.weshopify.platform.features.brands.exceptions.BrandNotFoundException;
import com.weshopify.platform.features.brands.repo.BrandRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BrandService {
	public static final int BRANDS_PER_PAGE = 10;

	@Autowired
	private BrandRepository repo;

	public List<Brand> listAll() {
		return (List<Brand>) repo.findAll();
	}

	public List<Brand> listByPage(int pageNum, int noOfRecPerPage) {
		Pageable pageable = PageRequest.of(pageNum, noOfRecPerPage);
		// helper.listEntities(pageNum, BRANDS_PER_PAGE, repo);
		return repo.findAll(pageable).getContent();
	}

	public BrandBean save(BrandBean brandBean) {
		
		Brand brand = mapBeanToDomain(brandBean);
		brand = repo.save(brand);
		brandBean = mapDomainToBean(brand);
		return brandBean;
	}

	private BrandBean mapDomainToBean(Brand brandDomain) {
		BrandBean brandBean = new BrandBean();
		BeanUtils.copyProperties(brandDomain, brandBean);
		Set<Category> catset = brandDomain.getCategories();
		if (catset != null && catset.size() > 0) {
			Set<CategoryBean> catSet = new HashSet<>();
			catset.forEach(cat -> {
				CategoryBean catBean = new CategoryBean();
				System.out.println("categories data is:\t" + cat.getName());
				BeanUtils.copyProperties(cat, catBean);
				catSet.add(catBean);

			});
			brandBean.setCategories(catSet);
		}
		return brandBean;
	}
	private Brand mapBeanToDomain(BrandBean brandBean) {
		Brand brand = new Brand();
		BeanUtils.copyProperties(brandBean, brand);
		Set<CategoryBean> catset = brandBean.getCategories();
		if (catset != null && catset.size() > 0) {
			Set<Category> catDomainSet = new HashSet<>();
			catset.forEach(catBean -> {
				Category catDomain = new Category();
				BeanUtils.copyProperties(catBean, catDomain);
				catDomainSet.add(catDomain);

			});
			brand.setCategories(catDomainSet);
		}
		return brand;
	}
	
	
	public BrandBean get(Integer id) throws BrandNotFoundException {
		try {

			Optional<Brand> optional = repo.findById(id);
			if (optional.isPresent()) {
				log.info("Brand with the ID :\t" + id + "\t found");
				Brand brandDomain = optional.get();
				BrandBean brandBean = mapDomainToBean(brandDomain);
				return brandBean;
			}
			throw new BrandNotFoundException("Could not find any brand with ID " + id);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new BrandNotFoundException("Could not find any brand with ID " + id);
		}
	}

	public List<Brand> delete(Integer id) throws BrandNotFoundException {
		Long countById = repo.countById(id);

		if (countById == null || countById == 0) {
			throw new BrandNotFoundException("Could not find any brand with ID " + id);
		}

		repo.deleteById(id);

		return listAll();

	}

	public String checkUnique(Integer id, String name) {
		boolean isCreatingNew = (id == null || id == 0);
		Brand brandByName = repo.findByName(name);

		if (isCreatingNew) {
			if (brandByName != null)
				return "Duplicate";
		} else {
			if (brandByName != null && brandByName.getId() != id) {
				return "Duplicate";
			}
		}

		return "OK";
	}
}
