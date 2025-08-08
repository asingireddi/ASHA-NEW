package com.miraclesoft.scvp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.LifeCycle;
import com.miraclesoft.scvp.model.LifeCyclePayload;
import com.miraclesoft.scvp.service.LifeCycleService;

/**
 * The Class LifeCycleController.
 *
 * @author Narendar Geesidi
 */
@RestController
@RequestMapping("/lifecycle")
public class LifeCycleController {

    /** The life cycle service. */
    @Autowired
    private LifeCycleService lifeCycleService;

    /**
     * Warehouse order life cycle.
     *
     * @param depositorOrderNumber the depositor order number
     * @param database the database
     * @return the list
     */
    @PostMapping("/search")
    public CustomResponse lifeCycle(@RequestBody final LifeCyclePayload lifeCyclePayload) {
        return lifeCycleService.lifeCycle(lifeCyclePayload);
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
    @GetMapping("/detailInfo/{depositorOrderNumber}/{fileId}/{database}/{transaction}")
    public LifeCycle lifeCycleDetailInfo(@PathVariable final String depositorOrderNumber,
            @PathVariable final String fileId, @PathVariable final String database,
            @PathVariable final String transaction) {
        return lifeCycleService.lifeCycleDetailInfo(depositorOrderNumber, fileId, database, transaction);
    }

}
