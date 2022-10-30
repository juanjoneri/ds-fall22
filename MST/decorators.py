

def message(func):
    def wrapper(*args, **kwargs):
        args[0]._wakeup()
        func(*args, **kwargs)
    
    return wrapper

def procedure(func):
    return func 