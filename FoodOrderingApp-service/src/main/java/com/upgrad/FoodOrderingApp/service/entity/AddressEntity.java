package com.upgrad.FoodOrderingApp.service.entity;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "ADDRESS")
@NamedQueries({
        @NamedQuery(name = "addressByUuid", query = "select a from AddressEntity a where a.uuid = :uuid")
})
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
    private String flatBuilNo;

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
    @ColumnDefault("1")
    private Integer active;

    public AddressEntity() {
    }

    public AddressEntity(@NotNull @Size(max = 200) String uuid, @NotNull @Size(max = 255) String flatBuilNo,
                         @NotNull @Size(max = 200) String locality, @NotNull @Size(max = 200) String city,
                         @NotNull @Size(max = 200) String pincode, @NotNull StateEntity state) {
        this.uuid = uuid;
        this.flatBuilNo = flatBuilNo;
        this.locality = locality;
        this.city = city;
        this.pincode = pincode;
        this.state = state;
    }

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

    public String getFlatBuilNo() {
        return flatBuilNo;
    }

    public void setFlatBuilNo(String flatBuilNo) {
        this.flatBuilNo = flatBuilNo;
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

