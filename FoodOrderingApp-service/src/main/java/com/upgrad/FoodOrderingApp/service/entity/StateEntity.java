package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "STATE")
@NamedQueries({
        @NamedQuery(name = "stateByStateUuid", query = "select s from StateEntity s where s.uuid = :uuid"),
        @NamedQuery(name = "allStates", query = "select s from StateEntity s")
})

public class StateEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(name = "uuid", unique = true)
    @NotNull
    @Size(max = 200)
    private String uuid;


    @Column(name = "state_name")
    @NotNull
    @Size(max = 30)
    private String stateName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
}