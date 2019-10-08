package com.colusbisd.fonninez.viewmodel.businesspartnertype;

import android.app.Application;
import android.os.Parcelable;

import com.colusbisd.fonninez.viewmodel.EntityViewModel;
import com.sap.cloud.android.odata.foninez.BusinessPartnerType;
import com.sap.cloud.android.odata.foninez.foninezMetadata.EntitySets;

/*
 * Represents View model for BusinessPartnerType
 * Having an entity view model for each <T> allows the ViewModelProvider to cache and
 * return the view model of that type. This is because the ViewModelStore of
 * ViewModelProvider cannot not be able to tell the difference between EntityViewModel<type1>
 * and EntityViewModel<type2>.
 */
public class BusinessPartnerTypeViewModel extends EntityViewModel<BusinessPartnerType> {

    /**
    * Default constructor for a specific view model.
    * @param application - parent application
    */
    public BusinessPartnerTypeViewModel(Application application) {
        super(application, EntitySets.businessPartner, BusinessPartnerType.codigoBPCRM);
    }

    /**
    * Constructor for a specific view model with navigation data.
    * @param application - parent application
    * @param navigationPropertyName - name of the navigation property
    * @param entityData - parent entity (starting point of the navigation)
    */
	 public BusinessPartnerTypeViewModel(Application application, String navigationPropertyName, Parcelable entityData) {
        super(application, EntitySets.businessPartner, BusinessPartnerType.codigoBPCRM, navigationPropertyName, entityData);
    }
}
