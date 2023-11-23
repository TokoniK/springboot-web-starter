package com.tokonik.webstarter.exceptions;

import com.tokonik.webstarter.util.ServiceResponse;
import org.springframework.web.server.ResponseStatusException;

public class ResponseException extends ResponseStatusException {


    private final ServiceResponse<?> response;
    private final String path;

    public ResponseException(ServiceResponse<?> response, String path) {
        super(response.getStatusCode());
//        super(response.getStatus(), "["+response.getMessages().stream().reduce((o, o2) -> o+";\n"+o2).toString()+"]" );
        this.response = response;
        this.path = path;
        response.setPath(path);
    }

    public ServiceResponse<?> getResponse() {
        return response;
    }

    public String getPath() {
        return path;
    }

//    public Map<String, JsonElement> getExceptionBody(){
//        HashMap<String,String> body = new HashMap<>();
//        Gson gson = new Gson();
//
//        JsonObject res = gson.toJsonTree(response).getAsJsonObject();
//        res.addProperty("path", path);
////        res.getAsJsonObject().entrySet().stream()
////                .map(e -> body.put(e.getKey(),e.getValue().toString()) );
////        body.put("path", path);
//
//        return res.asMap();
//    }

}
