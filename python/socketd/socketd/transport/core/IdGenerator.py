import uuid
from typing import Callable

IdGenerator = Callable[[None], str]

def GuidGenerator():
    return str(uuid.uuid4())