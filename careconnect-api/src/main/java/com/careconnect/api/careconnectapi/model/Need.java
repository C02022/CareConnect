package com.careconnect.api.careconnectapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class to represent a Need object
 */
public class Need {

    @JsonProperty("id") private int id;
    @JsonProperty("name") private String name;
    @JsonProperty("type") private String type; // Maybe make into ENUM ?
    @JsonProperty("quantity") private int quantity;
    @JsonProperty("cost") private int cost;

    /**
    * Creates a need with the given id, name, type, quantity, and cost
    *
    * @param id The id of the need
    * @param name The name of the need
    * @param type The need type
    * @param quantity The quantity needed
    * @param cost How much the need costs
    */
    public Need(@JsonProperty("id") int id,
    @JsonProperty("name") String name,
    @JsonProperty("type") String type,
    @JsonProperty("quantity") int quantity,
    @JsonProperty("cost") int cost)
    {
        this.id = id;
        this.name = name;
        this.type = type;
        this.quantity = quantity;
        this.cost = cost;
    }

    /**
     * Sets the ID of the Need
     * @param id ID of Need
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
    * Retrieves the ID of the Need
    * @return ID of the Need
    */
    public int getId(){
      return id;
    }

    /**
    * Retrieves the name of the Need
    * @return name of the Need
    */
    public String getName(){
      return name;
    }

    public void setName(String name){
      this.name = name;
    }

    /**
    * Retrieves the type of Need
    * @return type of Need
    */
    public String getType(){
      return type;
    }

    /**
    * Retrieves the quantity needed
    * @return quantity needed
    */
    public int getQuantity(){
      return quantity;
    }

    /**
    * Retrieves the cost of the need
    * @return cost of the Need
    */
    public int getCost(){
      return cost;
    }

    /**
    * Returns Need in "Need [id=%d, name=%s, type=%s, quantity=%d, cost=%d]" format
    * @return formatted Need string
    */
    public String toString(){
       String format = "Need [id=%d, name=%s, type=%s, quantity=%d, cost=%d]";
       return String.format(format, id, name, type, quantity, cost);
    }

    /**
     * Determines whether a need object is equal to another
     * Needs are equivalent if they have the same name, type, quantity and cost
     *
     * @param obj Other Need object
     * @return True if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() == Need.class) {
            Need newNeed = (Need) obj;
            return newNeed.name.equals(this.name)
                    && newNeed.quantity == this.quantity
                    && newNeed.type.equals(this.type)
                    && newNeed.cost == this.cost;
        }

        return false;
    }
}
