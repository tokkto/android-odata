package com.colusbisd.fonninez.mdui.asistencia;

import androidx.lifecycle.ViewModelProviders;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.colusbisd.fonninez.R;
import com.colusbisd.fonninez.app.ErrorHandler;
import com.colusbisd.fonninez.app.ErrorMessage;
import com.colusbisd.fonninez.app.SAPWizardApplication;
import com.colusbisd.fonninez.databinding.FragmentAsistenciaCreateBinding;
import com.colusbisd.fonninez.mdui.BundleKeys;
import com.colusbisd.fonninez.mdui.InterfacedFragment;
import com.colusbisd.fonninez.mdui.UIConstants;
import com.colusbisd.fonninez.repository.OperationResult;
import com.colusbisd.fonninez.viewmodel.asistenciatype.AsistenciaTypeViewModel;
import com.sap.cloud.mobile.fiori.object.ObjectHeader;
import com.sap.cloud.android.odata.foninez.AsistenciaType;
import com.sap.cloud.android.odata.foninez.foninezMetadata.EntityTypes;
import com.sap.cloud.android.odata.foninez.foninezMetadata.EntitySets;
import com.sap.cloud.mobile.fiori.formcell.SimplePropertyFormCell;
import com.sap.cloud.mobile.odata.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A fragment that presents a screen to either create or update an existing AsistenciaType entity.
 * This fragment is contained in the {@link AsistenciaActivity}.
 */
public class AsistenciaCreateFragment extends InterfacedFragment<AsistenciaType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsistenciaCreateFragment.class);
    //The key for the saved instance of the working entity for device configuration change
    private static final String KEY_WORKING_COPY = "WORKING_COPY";

    /** AsistenciaType object and it's copy: the modifications are done on the copied object. */
    private AsistenciaType asistenciaTypeEntity;
    private AsistenciaType asistenciaTypeEntityCopy;

    /** DataBinding generated class */
    private FragmentAsistenciaCreateBinding binding;

    /** Indicate what operation to be performed */
    private String operation;

    /** AsistenciaType ViewModel */
    private AsistenciaTypeViewModel viewModel;

    /** Application error handler to report error */
    private ErrorHandler errorHandler;

    /** The update menu item */
    private MenuItem updateMenuItem;

    /**
     * This fragment is used for both update and create for Asistencia to enter values for the properties.
     * When used for update, an instance of the entity is required. In the case of create, a new instance
     * of the entity with defaults will be created. The default values may not be acceptable for the
     * OData service.
     * Arguments: Operation: [OP_CREATE | OP_UPDATE]
     *            AsistenciaType if Operation is update
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menu = R.menu.itemlist_edit_options;
        setHasOptionsMenu(true);
        errorHandler = ((SAPWizardApplication)currentActivity.getApplication()).getErrorHandler();

        Bundle bundle = getArguments();
        operation = bundle.getString(BundleKeys.OPERATION);
        if (UIConstants.OP_CREATE.equals(operation)) {
            activityTitle = currentActivity.getResources().getString(R.string.title_create_fragment, EntityTypes.asistenciaType.getLocalName());
        } else {
            activityTitle = currentActivity.getResources().getString(R.string.title_update_fragment) + " " + EntityTypes.asistenciaType.getLocalName();
        }

        ((AsistenciaActivity)currentActivity).isNavigationDisabled = true;
        if(secondaryToolbar != null) {
            secondaryToolbar.setTitle(activityTitle);
        } else {
            currentActivity.setTitle(activityTitle);
        }

        viewModel = ViewModelProviders.of(currentActivity).get(AsistenciaTypeViewModel.class);
        viewModel.getCreateResult().observe(this, result -> onComplete(result));
        viewModel.getUpdateResult().observe(this, result -> onComplete(result));

        if(UIConstants.OP_CREATE.equals(operation)) {
            asistenciaTypeEntity = createAsistenciaType();
        } else {
            asistenciaTypeEntity = viewModel.getSelectedEntity().getValue();
        }

        AsistenciaType workingCopy = null;
        if( savedInstanceState != null ) {
            workingCopy =  (AsistenciaType)savedInstanceState.getParcelable(KEY_WORKING_COPY);
        }
        if( workingCopy == null ) {
            asistenciaTypeEntityCopy = (AsistenciaType) asistenciaTypeEntity.copy();
            asistenciaTypeEntityCopy.setEntityTag(asistenciaTypeEntity.getEntityTag());
            asistenciaTypeEntityCopy.setOldEntity(asistenciaTypeEntity);
            asistenciaTypeEntityCopy.setEditLink((asistenciaTypeEntity.getEditLink()));
        } else {
            //in this case, the old entity and entity tag should already been set.
            asistenciaTypeEntityCopy = workingCopy;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ObjectHeader objectHeader = currentActivity.findViewById(R.id.objectHeader);
        if( objectHeader != null ) objectHeader.setVisibility(View.GONE);
        return setupDataBinding(inflater, container);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(KEY_WORKING_COPY, asistenciaTypeEntityCopy);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_item:
                updateMenuItem = item;
                enableUpdateMenuItem(false);
                return onSaveItem();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** 
     * Enables or disables the update menu item base on the given 'enable'
     * @param enable true to enable the menu item, false otherwise
     */
    private void enableUpdateMenuItem(boolean enable) {
        updateMenuItem.setEnabled(enable);
        updateMenuItem.getIcon().setAlpha( enable ? 255 : 130);
    }

    /**
     * Saves the entity
     */
    private boolean onSaveItem() {
        if (!isAsistenciaTypeValid()) {
            return false;
        }
        //set 'isNavigationDisabled' false here to make sure the logic in list is ok, and set it to true if update fails.
        ((AsistenciaActivity)currentActivity).isNavigationDisabled = false;
        if( progressBar != null ) progressBar.setVisibility(View.VISIBLE);
        if (operation.equals(UIConstants.OP_CREATE)) {
            viewModel.create(asistenciaTypeEntityCopy);
        } else {
            viewModel.update(asistenciaTypeEntityCopy);
        }
        return true;
    }

    /**
     * Create a new AsistenciaType instance and initialize properties to its default values
     * Nullable property will remain null
     * @return new AsistenciaType instance
     */
    private AsistenciaType createAsistenciaType() {
        AsistenciaType asistenciaTypeEntity = new AsistenciaType(true);
        return asistenciaTypeEntity;
    }

    /** Callback function to complete processing when updateResult or createResult events fired */
    private void onComplete(@NonNull OperationResult<AsistenciaType> result) {
        if( progressBar != null ) progressBar.setVisibility(View.INVISIBLE);
        enableUpdateMenuItem(true);
        if (result.getError() != null) {
            ((AsistenciaActivity)currentActivity).isNavigationDisabled = true;
            handleError(result);
        } else {
            boolean isMasterDetail = currentActivity.getResources().getBoolean(R.bool.two_pane);
            if( UIConstants.OP_UPDATE.equals(operation) && !isMasterDetail) {
                viewModel.setSelectedEntity(asistenciaTypeEntityCopy);
            }
            currentActivity.onBackPressed();
        }
    }

    /** Simple validation: checks the presence of mandatory fields. */
    private boolean isValidProperty(@NonNull Property property, @NonNull String value) {
        boolean isValid = true;
        if (!property.isNullable() && value.isEmpty()) {
            isValid = false;
        }
        return isValid;
    }

    /**
     * Set up data binding for this view
     * @param inflater - layout inflater from onCreateView
     * @param container - view group from onCreateView
     * @return view - rootView from generated data binding code
     */
    private View setupDataBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        binding = FragmentAsistenciaCreateBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        binding.setAsistenciaType(asistenciaTypeEntityCopy);
        return rootView;
    }

    /** Validate the edited inputs */
    private boolean isAsistenciaTypeValid() {
        LinearLayout linearLayout = getView().findViewById(R.id.create_update_asistenciatype);
        boolean isValid = true;
        // validate properties i.e. check non-nullable properties are truly non-null
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View viewItem = linearLayout.getChildAt(i);
            SimplePropertyFormCell simplePropertyFormCell = (SimplePropertyFormCell)viewItem;
            String propertyName = (String) simplePropertyFormCell.getTag();
            Property property = EntityTypes.asistenciaType.getProperty(propertyName);
            String value = simplePropertyFormCell.getValue().toString();
            if (!isValidProperty(property, value)) {
                simplePropertyFormCell.setTag(R.id.TAG_HAS_MANDATORY_ERROR, true);
                String errorMessage = getResources().getString(R.string.mandatory_warning);
                simplePropertyFormCell.setErrorEnabled(true);
                simplePropertyFormCell.setError(errorMessage);
                isValid = false;
            }
            else {
                if (simplePropertyFormCell.isErrorEnabled()){
                    boolean hasMandatoryError = (Boolean)simplePropertyFormCell.getTag(R.id.TAG_HAS_MANDATORY_ERROR);
                    if (!hasMandatoryError) {
                        isValid = false;
                    } else {
                        simplePropertyFormCell.setErrorEnabled(false);
                    }
                }
                simplePropertyFormCell.setTag(R.id.TAG_HAS_MANDATORY_ERROR, false);
            }
        }
        return isValid;
    }

    /**
     * Notify user of error encountered while execution the operation
     * @param result - operation result with error
     */
    private void handleError(@NonNull OperationResult<AsistenciaType> result) {
        ErrorMessage errorMessage;
        switch (result.getOperation()) {
            case UPDATE:
                errorMessage = new ErrorMessage(getResources().getString(R.string.update_failed),
                        getResources().getString(R.string.update_failed_detail), result.getError(), false);
                break;
            case CREATE:
                errorMessage = new ErrorMessage(getResources().getString(R.string.create_failed),
                        getResources().getString(R.string.create_failed_detail), result.getError(), false);
                break;
            default:
                throw new AssertionError();
        }
        errorHandler.sendErrorMessage(errorMessage);
    }
}
