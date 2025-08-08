package com.miraclesoft.scvp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.scvp.model.Correlation;
import com.miraclesoft.scvp.service.CorrelationService;

/**
 * The Class CorrelationController.
 *
 * @author Priyanka Kolla
 */
@RestController
@RequestMapping("/correlation")
public class CorrelationController {

    /** The correlation service. */
    @Autowired
    private CorrelationService correlationService;

    /**
     * Save.
     *
     * @param correlation the correlation
     * @return the string
     * @throws Exception the exception
     */
    @PostMapping("/add")
    public String save(@RequestBody final Correlation correlation) throws Exception {
        return correlationService.save(correlation);
    }

    /**
     * Search.
     *
     * @param correlation the correlation
     * @return the list
     * @throws Exception the exception
     */
    @PostMapping("/search")
    public List<Correlation> search(@RequestBody final Correlation correlation) throws Exception {
        return correlationService.findAll(correlation);
    }

    /**
     * Delete.
     *
     * @param correlation the correlation
     * @return the string
     * @throws Exception the exception
     */
    @DeleteMapping("/delete")
    public String delete(@RequestBody final Correlation correlation) throws Exception {
        return correlationService.delete(correlation);
    }

}
