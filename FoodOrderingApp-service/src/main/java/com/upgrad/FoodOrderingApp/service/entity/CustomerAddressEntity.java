package com.upgrad.FoodOrderingApp.service.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "customer_address" , schema = "public")
@NamedQueries(
        {
                @NamedQuery(name = "getCustomerAddress" , query = "select c from CustomerAddressEntity c where c.customerId = :customerId"),
                @NamedQuery(name = "getAllCustomerAddress" , query = "select c from CustomerAddressEntity c ")
        }
)
public class CustomerAddressEntity implements Serializable{

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "customer_id")
    @NotNull
    private CustomerEntity customerId;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "address_id")
    @NotNull
    private AddressEntity addressId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CustomerEntity getCustomerId() {
        return customerId;
    }

    public void setCustomerId(CustomerEntity customerId) {
        this.customerId = customerId;
    }

    public AddressEntity getAddressId() {
        return addressId;
    }

    public void setAddressId(AddressEntity addressId) {
        this.addressId = addressId;
    }
}
