package com.upgrad.FoodOrderingApp.service.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "ADDRESS")
public class AddressEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(name = "uuid", unique = true)
    @NotNull
    @Size(max = 200)
    private String uuid;


    @Column(name = "flat_buil_Number")
    @NotNull
    @Size(max = 255)
    private String flat_building_name;


    @Column(name = "locality")
    @NotNull
    @Size(max = 200)
    private String locality;


    @Column(name = "city")
    @NotNull
    @Size(max = 200)
    private String city;


    @Column(name = "pincode")
    @NotNull
    @Size(max = 200)
    private String pincode;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "state_id")
    @NotNull
    private StateEntity state;


    @Column(name = "active")
    private Integer active;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFlat_building_name() {
        return flat_building_name;
    }

    public void setFlat_building_name(String flat_building_name) {
        this.flat_building_name = flat_building_name;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public StateEntity getState() {
        return state;
    }

    public void setState(StateEntity state) {
        this.state = state;
    }
}

