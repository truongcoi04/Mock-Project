package com.food_recipe.service;

import com.food_recipe.dto.RecipeDTO;
import com.food_recipe.dto.RecipeExchangeFormForCreating;
import com.food_recipe.entity.Point;
import com.food_recipe.entity.Recipe;
import com.food_recipe.entity.User;
import com.food_recipe.repository.PointRepository;
import com.food_recipe.repository.RecipeExchangeRepository;
import com.food_recipe.repository.RecipeRepository;
import com.food_recipe.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecipeExchangeService implements IRecipeExchangeService{

    @Autowired
    private RecipeExchangeRepository recipeExchangeRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    @Transactional
    public String createExchange(RecipeExchangeFormForCreating obj) {
        Recipe recipe = recipeRepository.findById(obj.getRecipeId()).get();
        User user = userRepository.findById(obj.getUserId()).get();

        try {

            if (recipe.getCreator().getId() == user.getId()){
                return "Bạn là người tạo công thức này" ;

            } else if(isExistsExchange(obj.getUserId(), obj.getRecipeId())){
                return "This exchange already existed!";
            }else {

                Integer recipePoint = recipe.getPoint();
                Point point = pointRepository.findByUserId(obj.getUserId());

                if (user.getPoint().getPoint() >= recipePoint){

                    point.setPoint(user.getPoint().getPoint() - recipePoint);

                    User userAuthor = userRepository.findById(recipe.getCreator().getId()).get();
                    Point pointAuthor = pointRepository.findByUserId(recipe.getCreator().getId());
                    pointAuthor.setPoint(userAuthor.getPoint().getPoint() + recipePoint);

                    pointRepository.save(point);
                    pointRepository.save(pointAuthor);

                }else {
                    return "Bạn không đủ Point,mời bạn xem công thức khác";
                }

                recipeExchangeRepository.save(obj.toEntity());
                return "Create Exchange successfully!";
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
            return "An error has occurred!";
        }
    }

    @Override
    public boolean isExistsExchange(Integer userId, Integer recipeId) {
        return recipeExchangeRepository.existsByUserIdAndRecipeId(userId, recipeId);
    }



}
