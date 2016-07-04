package space.cyclic.reference.pojo;

import javax.persistence.*;

@Entity
@Table(name = "COMPANY")
public class Company {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private int id;
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @Column(name = "address", nullable = false)
    private String address;
    @Column(name = "phone_number", nullable = false, length = 10)
    private long phoneNumber;

    public Company() {
    }

    public Company(String name, String address, long phoneNumber) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
