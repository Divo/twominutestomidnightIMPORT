//Again, not much to see here
public class Singleperson extends List{
   
   public Item getWeekday(){
     return new Item("Sandwich","Rashers","Apple","Milk","Wine"); 
   } //Hardest part was picking what to put on each shopping list.
     //Do I really think all single people are lushes who down a bottle of wine each night?

   public Item getWeekend(){
      return new Item("Pizza","Beer","Orange Juice","TV Guide","Vitamins");
   }

   public Item getSummer(){
      return new Item("Sun Protection","Ice","Swimsuit","Wine","Cooler");
   }

   public Item getWinter(){
      return new Item("Coat","Scarf","Attractive Hat","Coal","Oranges");
   }

   public String toString(){
      return "Single Person";
   }

}


