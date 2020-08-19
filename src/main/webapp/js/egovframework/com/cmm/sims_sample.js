// Ajax Sample

			$component.get($("#form"), {
				url: "${contextPath}/genericList.do",
				data: {
					searchJson : JSON.stringify(params),
					sqlid : "hdel.bsiot.rm.cdm.service.CdmDAO.selectCntlHistoryList"
				} ,
				success: function (data, status, xhr) {
					$gridCntlHistory.gridSetList(data.dataList);
					$("#historyCount").text(data.dataList.length);
				}
			});
			
			$.ajax({
				url: "/dashboard/genericlListJson.html",
				data: {sqlid: "dashboard.corona.emmc.search.sample"}, 
				async: false,
				success:  function(response){
					rtnList = response.dataList;
				}
			});
			
			
			$.ajax({
                url: "${contextPath}/genericSave.do",
                type: "POST",
                data: {
                    sqlid : "hdel.bsiot.rm.cdm.service.CdmDAO.deleteFaultSpecial",
                    searchJson : JSON.stringify(selrow)
                } , 
                async: false,			                    		
                success:  function(response){
                    if(response.result != "success"){
                        alert("에러가발생하였습니다.");
                        console.log(response.message);
                    }
                    else{
                        alert(project + " 의 Vip Fault Code 가 모두 삭제되었습니다.");
                        console.log("삭제성공: " + project );
                    }
                    
                }
            });


		var popParemeters = {
			"fn_set": function(){
						            if($("#filterPop").val() != "" ){
						                v_filterPop = JSON.parse($("#filterPop").val());
						                var v_filters = [];
						                $.each(v_filterPop ,function(field, data){
						                    var obj = {
						                        field : field ,
						                        // value: [].concat(data)
						                        value: data , 
						                        isArray : _.isArray(data)
						                    };
						                    v_filters.push( obj );
						                });
						                $("#searchJson").val(JSON.stringify({fields: v_filters}));                          
						            }            
						        }
		}
		
		
		
		// http://localhost:8081/genericListAjax.do?sqlid=groupManageDAO.selectGroupList&firstIndex=0&recordCountPerPage=20
		// http://localhost:8081/genericPageAjax.do?sqlid=SimsSample.selectCmmCode&rows=20&page=1