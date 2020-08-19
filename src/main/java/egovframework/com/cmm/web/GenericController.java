package egovframework.com.cmm.web;

/**
 * 컴포넌트 설치 후 설치된 컴포넌트들을 IncludedInfo annotation을 통해 찾아낸 후
 * 화면에 표시할 정보를 처리하는 Controller 클래스
 * <Notice>
 * 		개발시 메뉴 구조가 잡히기 전에 배포파일들에 포함된 공통 컴포넌트들의 목록성 화면에
 * 		URL을 제공하여 개발자가 편하게 활용하도록 하기 위해 작성된 것으로,
 * 		실제 운영되는 시스템에서는 적용해서는 안 됨
 *      실 운영 시에는 삭제해서 배포해도 좋음
 * <Disclaimer>
 * 		운영시에 본 컨트롤을 사용하여 메뉴를 구성하는 경우 성능 문제를 일으키거나
 * 		사용자별 메뉴 구성에 오류를 발생할 수 있음
 * @author 공통컴포넌트 정진오
 * @since 2011.08.26
 * @version 2.0.0
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일		  수정자		수정내용
 *  ----------   --------   ---------------------------
 *  2011.08.26   정진오            최초 생성
 *  2011.09.16   서준식            컨텐츠 페이지 생성
 *  2011.09.26     이기하		header, footer 페이지 생성
 *  2019.12.04   신용호            KISA 보안코드 점검 : Map<Integer, IncludedCompInfoVO> map를 지역변수로 수정
 * </pre>
 */

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import egovframework.com.cmm.IncludedCompInfoVO;
import egovframework.com.cmm.LoginVO;
import egovframework.com.cmm.annotation.IncludedInfo;
import egovframework.com.cmm.service.impl.CmmService;
import egovframework.com.cmm.util.EgovUserDetailsHelper;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GenericController {

	private ApplicationContext applicationContext;

	private static final Logger LOGGER = LoggerFactory.getLogger(GenericController.class);
	

	public void afterPropertiesSet() throws Exception {}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		
		LOGGER.info("EgovComIndexController setApplicationContext method has called!");
	}
	
	@Autowired
	private CmmService commonService;

	
	@RequestMapping(value = "/generic",method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView generic(@SuppressWarnings("rawtypes") @RequestParam Map<Object,Object> searchVO,Locale locale, Model model, HttpServletResponse response,HttpServletRequest request, ModelAndView mav) {
    	//parameter.put("pjtCodeList",req.getParameterValues("pjtCodeList"));
    	mav.addObject("searchVO",searchVO);
    	commonService.requestToVo(request, searchVO);
    	mav.setViewName(searchVO.get("viewName").toString());
    	mav.addObject("locale_language",response.getLocale().getLanguage());
        return mav;
    }
	
	@RequestMapping(value = "/genericListAjax" ,method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView genericListAjax(HttpServletRequest request,@RequestParam Map<Object,Object> searchVO ,Locale locale, Model model) {
    	
    	ModelAndView mav = new ModelAndView(); 
    	commonService.requestToVo(request, searchVO);
    	Object filterStr = searchVO.get("filters");
    	if(filterStr != null){
    		JSONObject filters = JSONObject.fromObject(filterStr.toString());
        	searchVO.put("filtersOrigin", filterStr.toString());
        	searchVO.put("filters", filters);
    	}
    	List<?> dataList = commonService.selectList(searchVO.get("sqlid").toString(),searchVO);
        mav.addObject("dataList", dataList);       
        mav.setViewName("jsonView");      
        

        return mav;
    }
	
	@RequestMapping(value = "/genericPageAjax",method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView genericPageAjax(HttpServletRequest request,HttpServletResponse response, @RequestParam Map<Object,Object> searchVO ,Locale locale, Model model) {
    	
    	ModelAndView mav = new ModelAndView(); 
    	//searchVO.put("pjtCodeList",request.getParameterValues("pjtCodeList"));
    	commonService.requestToVo(request, searchVO);
    	Object filterStr = searchVO.get("filters");
    	if(filterStr != null && !filterStr.equals("")){
    		JSONObject filters = JSONObject.fromObject(filterStr.toString());
        	searchVO.put("filtersOrigin", filterStr.toString());
        	searchVO.put("filters", filters);
    	}
    	searchVO.put("listType", "page");
    	List<?> dataList = commonService.selectList(searchVO.get("sqlid").toString(),searchVO);
    	mav.addObject("rows", dataList);
    	
    	
    	searchVO.put("listType", "total");
    	@SuppressWarnings("unchecked")
		Map<String,Object> paging = commonService.selectOne(searchVO.get("sqlid").toString(),searchVO);
    	mav.addObject("total", paging.get("TOTAL"));
    	mav.addObject("page", paging.get("PAGE"));
    	mav.addObject("records", paging.get("RECORDS"));
        
        mav.setViewName("jsonView");        

        return mav;
    }
	
	@RequestMapping(value = "/genericSaveJson" ,method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView genericlSaveJson(HttpServletRequest request,@RequestParam Map<Object,Object> searchVO ,Locale locale, Model model) {
    	ModelAndView mav = new ModelAndView(); 
    	commonService.requestToVo(request, searchVO);
    	try{
    		commonService.update(searchVO.get("sqlid").toString(), searchVO); 
    		//searchVO.put("result","success");
    		mav.addObject("result", "success");
    	}catch(Exception ex){
    		//searchVO.put("result","fail");
    		//searchVO.put("message",ex.getMessage());
    		mav.addObject("result", "fail");       
    		mav.addObject("message", ex.getMessage());       
    		
    	}
    	
        mav.setViewName("jsonView");        

        return mav;
    }
	
	
	

	
}
