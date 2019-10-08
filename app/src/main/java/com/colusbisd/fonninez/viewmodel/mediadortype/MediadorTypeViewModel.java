package com.colusbisd.fonninez.viewmodel.mediadortype;

import android.app.Application;
import android.os.Parcelable;

import com.colusbisd.fonninez.viewmodel.EntityViewModel;
import com.sap.cloud.android.odata.foninez.MediadorType;
import com.sap.cloud.android.odata.foninez.foninezMetadata.EntitySets;

/*
 * Represents View model for MediadorType
 * Having an entity view model for each <T> allows the ViewModelProvider to cache and
 * return the view model of that type. This is because the ViewModelStore of
 * ViewModelProvider cannot not be able to tell the difference between EntityViewModel<type1>
 * and EntityViewModel<type2>.
 */
public class MediadorTypeViewModel extends EntityViewModel<MediadorType> {

    /**
    * Default constructor for a specific view model.
    * @param application - parent application
    */
    public MediadorTypeViewModel(Application application) {
        super(application, EntitySets.mediador, MediadorType.nombre);
    }

    /**
    * Constructor for a specific view model with navigation data.
    * @param application - parent application
    * @param navigationPropertyName - name of the navigation property
    * @param entityData - parent entity (starting point of the navigation)
    */
	 public MediadorTypeViewModel(Application application, String navigationPropertyName, Parcelable entityData) {
        super(application, EntitySets.mediador, MediadorType.nombre, navigationPropertyName, entityData);
    }
}
