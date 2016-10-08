package com.mrwind.category.remote.windCloud.api;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

@Service
public class WindCloudBaseApi {

    @Value("#{SERVICE_PROP['windCloud.ip']}")
    protected String IP;
    
    @Value("#{SERVICE_PROP['windCloud.port']}")
    protected String port;
    
    @Value("#{SERVICE_PROP['windCloud.product']}")
    protected String product;

    public String getIP() {
        return IP;
    }

    public void setIP(String iP) {
        IP = iP;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }
    
    public String getUserIdByToken(String token ,HttpServletResponse response) {
        Client client = Client.create();
        WebResource webResource = client.resource("http://"+IP+":"+port+"/"+product+"/account/baseInfo/token");
        ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).
                header("Authorization","token " + token).get(ClientResponse.class);
        String executorUserId = null;
        if (clientResponse.getStatus()==200) {
            String textEntity = clientResponse.getEntity(String.class);
            JSONObject json = JSON.parseObject(textEntity);
            executorUserId = json.getString("id");
        } else {
            response.setStatus(401);
        }
        return executorUserId;
    }
}
