/*
 * Copyright 2008-2009 MOPAS(MINISTRY OF SECURITY AND PUBLIC ADMINISTRATION).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package egovframework.com.cmm.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HTMLTagFilter implements Filter{

	@SuppressWarnings("unused")
	private FilterConfig config;

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		//chain.doFilter(new HTMLTagFilterRequestWrapper((HttpServletRequest)request), response);
		
		//CKEDITOR 문자치환 예외처리 추가..........YHU
		HttpServletRequest req = (HttpServletRequest) request;
		String uri = req.getRequestURI().toString().trim();
		//게시판관리 - ckeditor , 통합링크관리 - htmlarea
		String urlList[] = {"/cop/bbs", "/uss/ion"}; // 예외처리할 URL - 여러 개일 경우 별도 처리 필요
		boolean allowedRequest = false;

		for(String url : urlList) {
			// 현재 uri 와 비교
			if (uri.startsWith(url)) {
				allowedRequest = true;
			}
		}
		if (!allowedRequest) {
			chain.doFilter(new HTMLTagFilterRequestWrapper((HttpServletRequest) request), response); // 필터링이 필요한 URI일 경우
																										// 요청값 그대로 처리
		} else {
			chain.doFilter(req, (HttpServletResponse) response); // 필터링을 하지 않을 경우 요청값 변경
		}
	}

	public void init(FilterConfig config) throws ServletException {
		this.config = config;
	}

	public void destroy() {

	}
}
