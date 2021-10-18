package com.example.demohilts.data.current

import kotlinx.coroutines.flow.MutableStateFlow

object CurrentData {

    var currentId: MutableStateFlow<Int> = MutableStateFlow(-1)
    var currentType = "";
}
