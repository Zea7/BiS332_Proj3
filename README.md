# BiS332_Proj3  

## 파일별 설명  
### src/api/interaction_request.py  
drug 간의 interaction을 UpToDate에서 받아오는 코드가 작성되어있습니다.  
requests의 get method를 활용하여 drug 이름 list를 넣으면 drug id를 받아오고,  
drug id를 가지고 drug 간의 interaction을 받아옵니다.  
  
### src/api/side_effect_request.py  
SIDER 사이트에서 drug name을 가지고 requests get method를 사용하여 html response를 받아오고,  
html parsing을 통해 drug id를 받아옵니다.  
이후 drug id를 다시 requests get method를 통해 모든 side effect를  
html parsing을 통해 정리한 후, 정보가 있는 side effect만 정리해서 dictionary 형태로 return 해줍니다.  
  
### src/Design/ui.py  
프로그램 실행 시, 사용의 편의성을 위하여 GUI 창을 띄워줍니다.  
사용하는 UI 파일들은 UI 디렉토리 내에 있습니다.  
