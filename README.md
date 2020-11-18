# DaCooker

DaCooker is a Recipe Management Android Application where the user can create, modify, visualize and delete recipes. Each one of them contains various steps to follow, the necesary ingredients and their quantities.

Functionalities
================

-Recipe CRUD. <br>
-Ingredients CRUD. <br>
-Filters by:
  - Recipe's name. <br>
  - Type of recipe (launch, dinner, others). <br>
  - Ingredient/s. <br>
  - Execution time. <br>

Moreover, the user can visualize most used recipes.

Data Base
==========
-Recipe: id, name, photo, mealType, executionTime, notes, timesCooked, (ingredienteList), (stepList). <br>
-Ingredient: id, id_recipe, name, quantity. <br>
-Step: id, id_recipe, stepOrder, description, requiredTimer, timerTime.
