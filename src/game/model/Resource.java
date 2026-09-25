package game.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents an abstract resource in the game (e.g., Coal, Wood, Meat).
 * Implements {@link Comparable} for sorting by quantity and
 * {@link Serializable} for saving/loading data.
 */
public abstract class Resource implements Comparable<Resource>, Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private double quantity;

    /**
     * Constructor to initialize a specific resource.
     * 
     * @param name     The name of the resource. Must not be null or empty.
     * @param quantity The initial quantity of the resource. Must not be negative.
     * @throws IllegalArgumentException if the name is null/empty or the quantity is
     *                                  negative.
     */
    protected Resource(String name, double quantity) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Resource name cannot be null or empty.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        this.name = name;
        this.quantity = quantity;
    }

    /**
     * Gets the name of the resource.
     * 
     * @return The name of the resource.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the current quantity of the resource.
     * 
     * @return The quantity of the resource.
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the resource.
     * 
     * @param quantity The new quantity for the resource.
     * @throws IllegalArgumentException if the quantity is negative.
     */
    public void setQuantity(double quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        this.quantity = quantity;
    }

    /**
     * Returns a string representation of the resource.
     * 
     * @return A string in the format "Name: Quantity".
     */
    @Override
    public String toString() {
        return name + ": " + quantity;
    }

    /**
     * Compares this resource with another resource based on their quantities.
     * 
     * @param other The other resource to compare to.
     * @return A negative integer, zero, or a positive integer as this resource's
     *         quantity is less than, equal to, or greater than the specified
     *         resource's quantity.
     */
    @Override
    public int compareTo(Resource other) {
        return Double.compare(this.quantity, other.quantity);
    }

    /**
     * Checks if two resources are equal based on their names.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Resource resource = (Resource) o;
        return name.equals(resource.name);
    }

    /**
     * Generates a hash code for the resource based on its name.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}