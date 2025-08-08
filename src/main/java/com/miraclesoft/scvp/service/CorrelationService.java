package com.miraclesoft.scvp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miraclesoft.scvp.model.Correlation;
import com.miraclesoft.scvp.service.impl.CorrelationServiceImpl;

/**
 * The Class CorrelationService.
 *
 * @author Priyanka Kolla
 */
@Service
public class CorrelationService {

    /** The correlation service impl. */
    @Autowired
    private CorrelationServiceImpl correlationServiceImpl;

    /**
     * Save.
     *
     * @param correlation the correlation
     * @return the string
     * @throws Exception the exception
     */
    public String save(final Correlation correlation) throws Exception {
        return correlationServiceImpl.save(correlation);
    }

    /**
     * Find all.
     *
     * @param correlation the correlation
     * @return the list
     * @throws Exception the exception
     */
    public List<Correlation> findAll(final Correlation correlation) throws Exception {
        return correlationServiceImpl.findAll(correlation);
    }

    /**
     * Delete.
     *
     * @param correlation the correlation
     * @return the string
     * @throws Exception the exception
     */
    public String delete(final Correlation correlation) throws Exception {
        return correlationServiceImpl.delete(correlation);
    }

}
