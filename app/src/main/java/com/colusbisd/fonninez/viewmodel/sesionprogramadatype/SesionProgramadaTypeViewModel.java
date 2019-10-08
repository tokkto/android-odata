package com.colusbisd.fonninez.viewmodel.sesionprogramadatype;

import android.app.Application;
import android.os.Parcelable;

import com.colusbisd.fonninez.viewmodel.EntityViewModel;
import com.sap.cloud.android.odata.foninez.SesionProgramadaType;
import com.sap.cloud.android.odata.foninez.foninezMetadata.EntitySets;

/*
 * Represents View model for SesionProgramadaType
 * Having an entity view model for each <T> allows the ViewModelProvider to cache and
 * return the view model of that type. This is because the ViewModelStore of
 * ViewModelProvider cannot not be able to tell the difference between EntityViewModel<type1>
 * and EntityViewModel<type2>.
 */
public class SesionProgramadaTypeViewModel extends EntityViewModel<SesionProgramadaType> {

    /**
    * Default constructor for a specific view model.
    * @param application - parent application
    */
    public SesionProgramadaTypeViewModel(Application application) {
        super(application, EntitySets.sesionProgramada, SesionProgramadaType.operacionID);
    }

    /**
    * Constructor for a specific view model with navigation data.
    * @param application - parent application
    * @param navigationPropertyName - name of the navigation property
    * @param entityData - parent entity (starting point of the navigation)
    */
	 public SesionProgramadaTypeViewModel(Application application, String navigationPropertyName, Parcelable entityData) {
        super(application, EntitySets.sesionProgramada, SesionProgramadaType.operacionID, navigationPropertyName, entityData);
    }
}
