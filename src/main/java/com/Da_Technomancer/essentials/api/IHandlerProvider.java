package com.Da_Technomancer.essentials.api;

public interface IHandlerProvider<A, B>{

	A getHandler(B param);
}
