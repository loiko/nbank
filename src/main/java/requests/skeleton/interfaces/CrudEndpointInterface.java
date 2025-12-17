package requests.skeleton.interfaces;

import models.BaseModel;

public interface CrudEndpointInterface {
    Object post(BaseModel model);

    Object get();

    Object get(long id);

    Object put(BaseModel model);

    Object delete(long id);
}
