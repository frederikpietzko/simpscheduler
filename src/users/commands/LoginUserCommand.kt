package com.github.frederikpietzko.users.commands

import com.github.frederikpietzko.framework.command.Command
import com.github.frederikpietzko.users.domain.ClearPassword
import com.github.frederikpietzko.users.domain.Username

data class LoginUserCommand(
    val username: Username,
    val password: ClearPassword,
) : Command