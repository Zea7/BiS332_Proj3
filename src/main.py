from api.interaction_request import get_interaction
from api.side_effect_request import get_id

names = ["aa-adefovir", "cladribine", "Tenofovir"]
get_interaction(names)

name = input("Input name: ")
get_id(name)