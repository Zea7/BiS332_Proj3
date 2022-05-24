from api.interaction_request import get_interaction
from api.side_effect_request import get_id

names = ["aa-adefovir", "cladribine"]
get_interaction(names)

name = input("Input name: ")
while not get_id(name):
    name=input("Input name: ")