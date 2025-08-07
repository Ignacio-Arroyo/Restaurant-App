package restaurante.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import restaurante.backend.entity.*;
import restaurante.backend.repository.DrinkRepository;
import restaurante.backend.repository.MealRepository;
import restaurante.backend.repository.ProductRepository;
import restaurante.backend.repository.UserRepository;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private DrinkRepository drinkRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeData();
    }

    private void initializeData() {
        // Create admin user
        if (userRepository.findByEmail("admin@restaurant.com").isEmpty()) {
            User admin = new User("Admin", "User", "admin@restaurant.com", passwordEncoder.encode("admin123"));
            admin.setRole(UserRole.ADMIN);
            userRepository.save(admin);
        }

        // Create sample meals
        if (mealRepository.count() == 0) {
            mealRepository.save(new Meal("Margherita Pizza", "Classic Italian pizza with tomato sauce, mozzarella, and fresh basil", 
                "Tomato sauce, mozzarella cheese, fresh basil, olive oil", "Gluten, Dairy", new BigDecimal("12.99"), MealType.VEGETARIAN));
            
            mealRepository.save(new Meal("Grilled Chicken Breast", "Tender grilled chicken breast with herbs and spices", 
                "Chicken breast, herbs, spices, olive oil", "None", new BigDecimal("16.99"), MealType.MEAT));
            
            mealRepository.save(new Meal("Vegan Buddha Bowl", "Nutritious bowl with quinoa, vegetables, and tahini dressing", 
                "Quinoa, mixed vegetables, tahini, lemon, herbs", "Sesame", new BigDecimal("14.99"), MealType.VEGAN));
            
            mealRepository.save(new Meal("Organic Salmon", "Fresh organic salmon with seasonal vegetables", 
                "Organic salmon, seasonal vegetables, herbs", "Fish", new BigDecimal("22.99"), MealType.BIO));
        }

        // Create sample drinks
        if (drinkRepository.count() == 0) {
            Drink cocaCola = new Drink("Coca Cola", "Classic cola soft drink", new BigDecimal("3.99"), DrinkType.SODA);
            cocaCola.setSize("MEDIUM");
            drinkRepository.save(cocaCola);
            
            Drink orangeJuice = new Drink("Fresh Orange Juice", "Freshly squeezed orange juice", new BigDecimal("5.99"), DrinkType.NATURAL);
            orangeJuice.setSize("LARGE");
            drinkRepository.save(orangeJuice);
            
            Drink houseWine = new Drink("House Wine", "Red house wine selection", new BigDecimal("8.99"), DrinkType.ALCOHOL);
            houseWine.setSize("SMALL");
            drinkRepository.save(houseWine);
            
            Drink sparklingWater = new Drink("Sparkling Water", "Premium sparkling water", new BigDecimal("2.99"), DrinkType.NATURAL);
            sparklingWater.setSize("MEDIUM");
            drinkRepository.save(sparklingWater);
        }

        // Create sample products for inventory
        if (productRepository.count() == 0) {
            productRepository.save(new Product("Tomatoes", "Fresh organic tomatoes", new BigDecimal("3.50"), 50, "kg"));
            productRepository.save(new Product("Chicken Breast", "Fresh chicken breast", new BigDecimal("8.99"), 25, "kg"));
            productRepository.save(new Product("Mozzarella Cheese", "Fresh mozzarella cheese", new BigDecimal("12.99"), 15, "kg"));
            productRepository.save(new Product("Quinoa", "Organic quinoa", new BigDecimal("6.99"), 20, "kg"));
        }
    }
}
