package ru.yamoney.test.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.yamoney.test.entity.Group;
import ru.yamoney.test.entity.GroupInstance;
import ru.yamoney.test.entity.KeyValue;
import ru.yamoney.test.service.TestSettingService;
import ru.yamoney.test.utils.Ajax;
import ru.yamoney.test.utils.RestException;

import java.util.List;
import java.util.Map;

/**
 * Created by def on 14.02.16.
 */

@Controller
public class TestSettingController extends ExceptionHandlerController {
    private static final Logger LOG = Logger.getLogger(TestSettingController.class);

    @Autowired
    @Qualifier("testSettingService")
    private TestSettingService testSettingService;

    @RequestMapping(value = "/group/fetch", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> getGroups() throws RestException {
        try {
            List<Group> result = testSettingService.getGroups();
            return Ajax.successResponse(result);
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = "/instance/{groupId}", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> getGroupInstance(@PathVariable("groupId") String groupIdStr) throws RestException {
        try {
            LOG.info("Incoming group: " + groupIdStr);
            long groupId = Long.parseLong(groupIdStr);
            List<GroupInstance> result = testSettingService.getGroupInstances(groupId);
            return Ajax.successResponse(result);
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = "/parameters/{instanceId}", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> getParameters(@PathVariable("instanceId") Long instanceId,
                                      @RequestParam("filter") String filter) throws RestException {
        try {
            LOG.info("Incoming instanceId: " + instanceId);
            if (filter == null) {
                return Ajax.successResponse(testSettingService.getParametersMap(instanceId));
            } else {
                return Ajax.successResponse(testSettingService.getParametersMap(instanceId, filter));
            }
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = "/group", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> saveGroup(@RequestParam("groupName") String groupName,
                                  @RequestParam("description") String description) throws RestException {
        try {
            testSettingService.addGroup(groupName, description);
            return Ajax.emptyResponse();
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = "/groupInstance", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> saveGroupInstance(
            @RequestParam("groupId") Long groupId,
            @RequestParam("groupInstanceName") String groupInstanceName,
            @RequestParam("description") String description) throws RestException {
        try {
            testSettingService.addGroupInstance(groupId, groupInstanceName, description);
            return Ajax.emptyResponse();
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = "/setParameter", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> saveGroupInstance(
            @RequestParam("instanceId") Long instanceId,
            @RequestParam("parameterId") Long parameterId,
            @RequestParam("value") String value) throws RestException {
        try {
            testSettingService.setParameter(instanceId, parameterId, value);
            return Ajax.emptyResponse();
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = "/addParameter", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> addParameter(
            @RequestParam("groupId") Long groupId,
            @RequestParam("name") String name,
            @RequestParam("description") String description) throws RestException {
        try {
            testSettingService.addParameter(groupId, name, description);
            return Ajax.emptyResponse();
        } catch (Exception e) {
            throw new RestException(e);
        }
    }
}
