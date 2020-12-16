package com.sensorweb.datacenterproductservice.controller;

import com.sensorweb.datacenterproductservice.entity.Product;
import com.sensorweb.datacenterproductservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping(value = "/product/getByServiceAndTime")
    @ResponseBody
    public Map<String, List<Product>> getProductInfoByID(@RequestParam("serviceName") String serviceName, @RequestParam("manufactureDate") String time) {
        Map<String, List<Product>> res = new HashMap<>();
        List<Product> info = productService.getproductByServiceAndTime(serviceName, time);
        res.put("ProductInfo", info);
        return res;
    }

    @PostMapping(value = "/product/insert")
    @ResponseBody
    public void InsertProduct(@RequestBody Product info) {
       productService.InsertProduct(info);
    }


}
