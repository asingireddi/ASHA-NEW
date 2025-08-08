package com.miraclesoft.scvp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.scvp.model.Scheduler;
import com.miraclesoft.scvp.service.SchedulerService;

/**
 * The Class SchedulerController.
 *
 * @author Priyanka Kolla
 */
@RestController
@RequestMapping("/scheduler")
public class SchedulerController {

    /** The scheduler service. */
    @Autowired
    private SchedulerService schedulerService;

    /**
     * Gets the schedulers.
     *
     * @param scheduler the scheduler
     * @return the scheduler list
     */
    @PostMapping("/search")
    public List<Scheduler> schedulers(@RequestBody final Scheduler scheduler) {
        return schedulerService.findAll(scheduler);
    }

    /**
     * Adds the scheduler.
     *
     * @param scheduler the scheduler
     * @return the string
     * @throws Exception the exception
     */
    @PostMapping("/add")
    public String addScheduler(@RequestBody final Scheduler scheduler) throws Exception {
        return schedulerService.save(scheduler);
    }

    /**
     * Gets the scheduler.
     *
     * @param id the id
     * @return the scheduler
     */
    @GetMapping("/{id}")
    public Scheduler getScheduler(@PathVariable final Long id) {
        return schedulerService.findOne(id);
    }

    /**
     * Update scheduler.
     *
     * @param scheduler the scheduler
     * @return the string
     * @throws Exception the exception
     */
    @PostMapping("/update")
    public String updateScheduler(@RequestBody final Scheduler scheduler) throws Exception {
        return schedulerService.update(scheduler);
    }

    /**
     * Delete.
     *
     * @param id the id
     * @return the string
     * @throws Exception the exception
     */
    @DeleteMapping("/{id}")
    public String delete(@PathVariable final Long id) throws Exception {
        return schedulerService.delete(id);
    }
}
