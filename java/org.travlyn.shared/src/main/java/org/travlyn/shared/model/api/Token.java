package org.travlyn.shared.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import org.travlyn.shared.model.db.TokenEntity;

import java.util.Objects;

/**
 * Token
 */
@Validated
public class Token extends AbstractDataTransferObject {
    @JsonProperty("id")
    @ApiModelProperty(value = "Identifier", required = true, example = "123")
    private int id = -1;

    @JsonProperty("token")
    @ApiModelProperty(value = "Token string", required = true, example = "re7sr75a<7dfg8df6g84bcd5f1v6a8sx")
    private String token = null;

    @JsonProperty("ip_address")
    @ApiModelProperty(value = "IP Address of Token's User", required = true, example = "192.168.0.1")
    private String ipAddress = null;

    public Token id(int id) {
        this.id = id;
        return this;
    }

    /**
     * Get id
     *
     * @return id
     **/
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Token token(String token) {
        this.token = token;
        return this;
    }

    /**
     * Get token
     *
     * @return token
     **/
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Token ipAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    /**
     * Get ipAddress
     *
     * @return ipAddress
     **/
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Token token = (Token) o;
        return Objects.equals(this.id, token.id) &&
                Objects.equals(this.token, token.token) &&
                Objects.equals(this.ipAddress, token.ipAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, token, ipAddress);
    }

    @Override
    public TokenEntity toEntity() {
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setId(this.id);
        tokenEntity.setToken(this.token);
        tokenEntity.setIpAddress(this.ipAddress);
        return tokenEntity;
    }
}
