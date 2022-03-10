package com.digitusforum.firewall.module;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.digitusforum.firewall.course.CourseVO;
import com.digitusforum.firewall.login.TokenVO;
import com.digitusforum.firewall.util.RequestService;

@RestController
public class FirewallModuleController {
	@Autowired
	ModuleRequestService moduleRequestService;
	@Autowired
	RequestService requestService;

	@CrossOrigin
	@PostMapping(value = "/firewall/module/v1/create")
	public ModuleVO create(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody ModuleVO moduleVO) {
		TokenVO tokenVO = requestService.validateToken(authorization, locale);
		return moduleRequestService.create(moduleVO, locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/module/v1/retrieveById")
	public ModuleVO retrieveById(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization, @RequestBody ModuleVO moduleVO) {
		TokenVO tokenVO = requestService.validateToken(authorization, locale);
		return moduleRequestService.retrieveById(moduleVO, locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/module/v1/retrieveByCourseId")
	public List<ModuleVO> retrieveByCourseId(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization, @RequestBody ModuleVO moduleVO) {
		TokenVO tokenVO = requestService.validateToken(authorization, locale);
		return moduleRequestService.retrieveByCourseId(moduleVO, locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/module/v1/retrieveByCourseIdWithVideos")
	public List<ModuleVO> retrieveModulesWithVideosByCourseId(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization, @RequestBody ModuleVO moduleVO) {
		TokenVO tokenVO = requestService.validateToken(authorization, locale);
		return moduleRequestService.retrieveByCourseIdWithVideos(moduleVO, locale);
	}
	
	@CrossOrigin
	@PostMapping(value = "/firewall/module/v1/update")
	public ModuleVO update(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization, @RequestBody ModuleVO moduleVO) {
		TokenVO tokenVO = requestService.validateToken(authorization, locale);
		return moduleRequestService.update(moduleVO, locale);
	}
	
	@CrossOrigin
	@PostMapping(value = "/firewall/module/v1/delete")
	public ModuleVO delete(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization, @RequestBody ModuleVO moduleVO) {
		TokenVO tokenVO = requestService.validateToken(authorization, locale);
		return moduleRequestService.delete(moduleVO, locale);
	}
	
	@CrossOrigin
	@PostMapping(value = "/firewall/module/v1/reorder")
	public ModuleVO reorder(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization, @RequestBody ModuleVO moduleVO) {
		TokenVO tokenVO = requestService.validateToken(authorization, locale);
		return moduleRequestService.reorder(moduleVO, locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/module/v1/addVideo")
	public ModuleVideoVO addVideo(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody ModuleVideoVO moduleVideoVO) {
		TokenVO tokenVO = requestService.validateToken(authorization, locale);
		return moduleRequestService.addVideo(moduleVideoVO, locale);
	}
	
	@CrossOrigin
	@PostMapping(value = "/firewall/module/v1/removeVideo")
	public ModuleVO removeVideo(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody ModuleVO moduleVO) {
		TokenVO tokenVO = requestService.validateToken(authorization, locale);
		return moduleRequestService.removeVideo(moduleVO, locale);
	}

	/*
	 * @CrossOrigin
	 * 
	 * @GetMapping(value = "/firewall/course/v1/retrieveAll") public List<CourseVO>
	 * retrieve(@RequestHeader(defaultValue = "en_us") String locale,
	 * 
	 * @RequestHeader String authorization) { TokenVO tokenVO =
	 * requestService.validateToken(authorization, locale); return
	 * courseService.retrieveAll(locale); }
	 * 
	 * @CrossOrigin
	 * 
	 * @GetMapping(value = "/firewall/course/v1/retrieveById") public CourseVO
	 * retrieve(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader
	 * String authorization,
	 * 
	 * @RequestBody CourseVO courseVO) { TokenVO tokenVO =
	 * requestService.validateToken(authorization, locale); return
	 * courseService.retrieveById(courseVO, locale); }
	 * 
	 * @CrossOrigin
	 * 
	 * @GetMapping(value =
	 * "/firewall/course/v1/retrieveModulesWithVideosByCourseId") public
	 * List<ModuleVO>
	 * retrieveModulesWithVideosByCourseId(@RequestHeader(defaultValue = "en_us")
	 * String locale,
	 * 
	 * @RequestHeader String authorization, @RequestBody CourseVO courseVO) {
	 * TokenVO tokenVO = requestService.validateToken(authorization, locale); return
	 * courseService.retrieveModulesWithVideosByCourseId(courseVO, locale); }
	 */

}
