package com.colusbisd.fonninez.viewmodel.motivotype;

import android.app.Application;
import android.os.Parcelable;

import com.colusbisd.fonninez.viewmodel.EntityViewModel;
import com.sap.cloud.android.odata.foninez.MotivoType;
import com.sap.cloud.android.odata.foninez.foninezMetadata.EntitySets;

/*
 * Represents View model for MotivoType
 * Having an entity view model for each <T> allows the ViewModelProvider to cache and
 * return the view model of that type. This is because the ViewModelStore of
 * ViewModelProvider cannot not be able to tell the difference between EntityViewModel<type1>
 * and EntityViewModel<type2>.
 */
public class MotivoTypeViewModel extends EntityViewModel<MotivoType> {

    /**
    * Default constructor for a specific view model.
    * @param application - parent application
    */
    public MotivoTypeViewModel(Application application) {
        super(application, EntitySets.motivo, MotivoType.motivoDesc);
    }

    /**
    * Constructor for a specific view model with navigation data.
    * @param application - parent application
    * @param navigationPropertyName - name of the navigation property
    * @param entityData - parent entity (starting point of the navigation)
    */
	 public MotivoTypeViewModel(Application application, String navigationPropertyName, Parcelable entityData) {
        super(application, EntitySets.motivo, MotivoType.motivoDesc, navigationPropertyName, entityData);
    }
}
