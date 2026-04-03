-- VMP 模块使用示例
-- Proto 共享功能演示

print("========================================")
print("VMP 模块使用示例")
print("========================================")

-- 创建一个 VMP 虚拟环境
print("\n1. 创建 VMP 虚拟环境:")
local vmp = vmp.create()
print("   vmp = vmp.create()")
print("   结果:", dump(vmp))

-- 启用 Proto 共享功能（允许多个 VM 共享相同函数原型）
print("\n2. 启用 Proto 共享功能:")
vmp:sprotoshare(true)
print("   vmp:sprotoshare(true)")
print("   结果:", dump(vmp))

-- 执行简单代码并返回结果
print("\n3. 执行简单代码:")
local result = vmp:exec("return 8")
print("   vmp:exec(\"return 8\")")
print("   结果:", result)

-- 执行算术运算
print("\n4. 执行算术运算:")
result = vmp:exec("return 10 + 20 * 2")
print("   vmp:exec(\"return 10 + 20 * 2\")")
print("   结果:", result)

-- 定义函数并执行
print("\n5. 定义并执行函数:")
vmp:exec([[
    function add(a, b)
        return a + b
    end
]])
result = vmp:exec("return add(100, 200)")
print("   vmp:exec([[function add(a, b) return a + b end]])")
print("   vmp:exec(\"return add(100, 200)\")")
print("   结果:", result)

-- 设置全局变量
print("\n6. 设置全局变量:")
vmp:set("global_var", "Hello from VMP!")
print("   vmp:set(\"global_var\", \"Hello from VMP!\")")
result = vmp:exec("return global_var")
print("   vmp:exec(\"return global_var\")")
print("   结果:", result)

-- 获取全局变量
print("\n7. 获取全局变量:")
result = vmp:get("global_var")
print("   vmp:get(\"global_var\")")
print("   结果:", result)

-- 使用 Proto 共享创建第二个 VM
print("\n8. 创建第二个 VM 并测试 Proto 共享:")
local vmp2 = vmp.create()
vmp2:sprotoshare(true)
print("   vmp2 = vmp.create()")
print("   vmp2:sprotoshare(true)")
print("   结果:", dump(vmp2))

-- 在第二个 VM 中执行相同的函数代码（应该复用已共享的 Proto）
print("\n9. 在第二个 VM 中执行相同的函数代码:")
vmp2:exec([[
    function add(a, b)
        return a + b
    end
]])
result = vmp2:exec("return add(50, 60)")
print("   vmp2:exec([[function add(a, b) return a + b end]])")
print("   vmp2:exec(\"return add(50, 60)\")")
print("   结果:", result)

-- 关闭 VM
print("\n10. 关闭 VMP 虚拟环境:")
vmp:close()
vmp2:close()
print("   vmp:close()")
print("   vmp2:close()")

print("\n========================================")
print("示例执行完成!")
print("========================================")
