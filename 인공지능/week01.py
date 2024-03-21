import time

start=time.time()
sum=0
for i in range(1,100000001):
    sum=sum+i
end=time.time()

print(end-start)

print(12+37)
print('python'+'is exciting')

import random
a=random.randint(10,20)
b=random.randint(10,20)

sum=a+b

print(a,b,sum)