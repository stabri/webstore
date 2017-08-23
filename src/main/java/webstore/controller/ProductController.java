package webstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import webstore.domain.Product;
import webstore.service.ProductService;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @RequestMapping
    public String list(Model model) {
        model.addAttribute("products",
                productService.getAllProducts());

        return "products";
    }

    @RequestMapping("/all")
    public String allProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products";
    }

    @RequestMapping("/{category}")
    public String getProductByCategory(Model model, @PathVariable("category") String productCatetgory) {
        model.addAttribute("products", productService.getProductsByCategory(productCatetgory));
        return "products";
    }

    @RequestMapping("/filter/{ByCriteria}")
    public String getProductsByCriteria(@MatrixVariable(pathVar = "ByCriteria") Map<String, List<String>> filterParams,
                                        Model model) {
        model.addAttribute("products", productService.getProductsByFilter(filterParams));
        return "products";
    }

    @RequestMapping("/product")
    public String getProductById(@RequestParam("id") String productId, Model model) {
        model.addAttribute("product", productService.getProductById(productId));

        return "product";
    }

    @RequestMapping("/{category}/{prices}")
    public String getProductByMultipleCriteria(@PathVariable("category")String category,
                                               @MatrixVariable(pathVar = "prices")Map<String, String> prices,
                                               @RequestParam("manufacturer")String man,
                                               Model model) {
        List<Product> byCategory = productService.getProductsByCategory(category);
        List<Product> byManufacturer = productService.getProductByManufacturer(man);

        Set<Product> pries = productService.getProductsByPriceFilter(prices);

        Set<Product> finProducts = new HashSet<>();
        finProducts.addAll(byCategory);
        finProducts.addAll(byManufacturer);
        finProducts.addAll(pries);

        model.addAttribute("products", finProducts);

        return "products";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String getAddNewProductFrom(@ModelAttribute("newProduct") Product newProduct) {
        return "addProduct";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAddNewProductFrom(@ModelAttribute("newProduct") Product newProduct,
                                           BindingResult result) {
        String[] suppressedFields = result.getSuppressedFields();
        if (suppressedFields.length > 0) {
            throw new RuntimeException("Próba wiazania niedozwolonych pól"
                    + StringUtils.arrayToCommaDelimitedString(suppressedFields));
        }
        productService.addProduct(newProduct);
        return "redirect:/products";
    }
    @InitBinder
    public void initialiseBinder(WebDataBinder binder) {
        binder.setDisallowedFields("unitsInOrder", "discontinued");
    }
}
