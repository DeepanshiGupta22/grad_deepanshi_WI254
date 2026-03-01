package com;
interface I {
    void abc();
}

class A implements I {
    public void abc() {
        System.out.println("Inside abc");
    }
}