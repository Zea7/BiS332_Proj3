import requests

def __get_interaction_with_ids(ids):
    url = "https://www.uptodate.com/services/app/drug/interaction/search/json?"
    for _, id in enumerate(ids):
        assert id!=None, "Wrong Input!"
        url += "drug={}&".format(id)
    url += "search=null"
    response = requests.get(url)
    datas = response.json().get("data").get("searchResults")
    
    res = []
    print(ids)
    for result in datas:
        items = result.get("items")
        name= [items[0].get("name"), items[1].get("name")]
        rating = result.get("riskRating")
        
        data = {'name': name, 'rating':rating}
        res.append(data)
        
    return res
        
        

def __get_ids(name, size):
    response = requests.get("https://www.uptodate.com/services/app/drug/interaction/search/autocomplete/json?term={1}&page=1&pageSize={0}".format(size, name))
    datas = response.json().get("data").get("drugs")
    print(datas)
    if datas == None:
        return False
    if len(datas) != 1:
        return False
    else:
        return datas[0]['id']
       

def __count_drugs():
    response = requests.get("https://www.uptodate.com/services/app/contents/table-of-contents/drug-information/all-drug-information/json?topicUrlType=webRelative&urlType=webRelative&languageCode=undefined")
    datas = response.json()
    data = datas.get("data")["items"]
    count = 0
    # for i in data:
    #     if i['name'].lower().startswith('a'):
    #         count+=1
    # print(count)
    print(datas)
    
def get_interaction(names):
    ids = []
    
    for _, name in enumerate(names):
        id = __get_ids(name, 10)
        print(id)
        if id != False:
            ids.append(__get_ids(name, 10))
    
  
    return __get_interaction_with_ids(ids)
    
if __name__=="__main__":
    names = ["aa-adefovir", "cladribine", "Tenofovir Products"]
    get_interaction(names)