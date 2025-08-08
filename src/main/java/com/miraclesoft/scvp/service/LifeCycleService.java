package com.miraclesoft.scvp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.LifeCycle;
import com.miraclesoft.scvp.model.LifeCyclePayload;
import com.miraclesoft.scvp.service.impl.LifeCycleServiceImpl;

/**
 * The Class LifeCycleService.
 *
 * @author Narendar Geesidi
 */
@Service
public class LifeCycleService {

    /** The life cycle service impl. */
    @Autowired
    private LifeCycleServiceImpl lifeCycleServiceImpl;

    /**
     * Warehouse order life cycle.
     *
     * @param depositorOrderNumber the depositor order number
     * @param database the database
     * @return the list
     */
    public CustomResponse lifeCycle(final LifeCyclePayload lifeCyclePayload) {
        return lifeCycleServiceImpl.lifeCycle(lifeCyclePayload);
    }

    /**
     * Warehouse order life cycle detail info.
     *
     * @param depositorOrderNumber the depositor order number
     * @param fileId the file id
     * @param database the database
     * @param transaction the transaction
     * @return the life cycle
     */
    public LifeCycle lifeCycleDetailInfo(final String depositorOrderNumber, final String fileId,
            final String database, final String transaction) {
        return lifeCycleServiceImpl.lifeCycleDetailInfo(depositorOrderNumber, fileId, database,
                transaction);
    }

}
