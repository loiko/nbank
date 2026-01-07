package api.requests.skeleton.interfaces;

import api.models.BaseModel;

public interface CrudEndpointInterface {
    Object post(BaseModel model);

    Object get();

    Object get(long id);

    Object put(BaseModel model);

    Object delete(long id);
}
