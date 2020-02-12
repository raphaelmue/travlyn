package org.travlyn.shared.model.db;

import org.travlyn.shared.model.api.Token;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "token")
public class TokenEntity implements DataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(targetEntity = UserEntity.class)
    private UserEntity user;

    @Column(name = "token")
    private String token;

    @Column(name = "expire_date")
    private LocalDate expireDate;

    @Column(name = "ip_address")
    private String ipAddress;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public Token toDataTransferObject() {
        return new Token().id(this.id).token(this.token).ipAddress(this.ipAddress);
    }
}
