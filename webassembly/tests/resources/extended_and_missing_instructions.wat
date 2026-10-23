;; Wat
;;
;; Copyright 2025 MicroEJ Corp. All rights reserved.
;; Use of this source code is governed by a BSD-style license that can be found with this software.

;; This code is not intended to be executed; it is only compiled to wasm for testing purposes.

(module
  (memory 0)
  (table $t_func 1 funcref)
  (elem funcref (ref.func 0))
  (elem func 1)
  (data $data "My data")

  (func $func (result i32)
    i32.const 0
  )

  ;; MicroEJ implemented extended Wasm instructions.
  (func
    i32.const 0
    i32.extend8_s
    drop
  )

  (func
    i32.const 0
    i32.extend16_s
    drop
  )
  
  (func
    i64.const 0
    i64.extend8_s
    drop
  )
  
  (func
    i64.const 0
    i64.extend16_s
    drop
  )
  (func
    i64.const 0
    i64.extend32_s
    drop
  )
  (func
    i32.const 0
    i32.const 0
    i32.const 0
    memory.init 0
  )
  (func
    i32.const 0
    i32.const 0
    i32.const 0
    memory.copy
  )
  
  (func
    i32.const 0
    i32.const 0
    i32.const 0
    memory.fill
  )
  (func
    i32.const 0
    i32.const 0
    memory.atomic.notify
    drop
  )
  (func
    i32.const 0
    i32.const 0
    i64.const 0
    memory.atomic.wait32
    drop
  )

  (func
  atomic.fence
  i32.const 0
  i32.atomic.load
  drop
  )

  (func
    i32.const 0   
    i64.atomic.load
    drop
  )
  (func
    i32.const 0
    i32.atomic.load8_u
    drop
  )
  (func
    i32.const 0    
    i32.atomic.load16_u
    drop
  )
  (func
    i32.const 0
    i64.atomic.load8_u
    drop
  )
  (func
    i32.const 0
    i64.atomic.load16_u
    drop
  )
  (func
    i32.const 0
    i64.atomic.load32_u
    drop
  )
  (func
    i32.const 0
    i32.const 0
    i32.atomic.store
  )

  (func
    i32.const 0
    i64.const 0
    i64.atomic.store
  )

  (func
    i32.const 0
    i32.const 0
    i32.atomic.store8
  )
  (func
    i32.const 0
    i32.const 0
    i32.atomic.store16
  )
  
  (func
    i32.const 0
    i64.const 0
    i64.atomic.store8
  )

  (func
    i32.const 0
    i64.const 0
    i64.atomic.store16
  )

  (func
    i32.const 0
    i64.const 0
    i64.atomic.store32
  )
  (func
    i32.const 0
    i32.const 0
    i32.atomic.rmw.xchg
    drop
  )

  (func
    i32.const 0
    i32.const 0
    i32.const 0
    i32.atomic.rmw.cmpxchg
    drop
    data.drop $data
  )
  
  (func
  i32.const 0
  i32.const 0
  i32.atomic.rmw.add
  drop
  )

  (func
  i32.const 0
  i64.const 0
  i64.atomic.rmw.add
  drop
  )

  (func
  i32.const 0
  i32.const 0
  i32.atomic.rmw8.add_u
  drop
  )
  
  (func
  i32.const 0
  i32.const 0
  i32.atomic.rmw16.add_u
  drop
  )
    
  (func
  i32.const 0
  i64.const 0
  i64.atomic.rmw8.add_u
  drop
  )
    
  (func
  i32.const 0
  i64.const 0
  i64.atomic.rmw16.add_u
  drop
  )
    
  (func
  i32.const 0
  i64.const 0
  i64.atomic.rmw32.add_u
  drop
  )
    
  (func
  i32.const 0
  i32.const 0
  i32.atomic.rmw.sub
  drop
  )
    
  (func
  i32.const 0
  i64.const 0
  i64.atomic.rmw.sub
  drop
  )
    
  (func
  i32.const 0
  i32.const 0
  i32.atomic.rmw8.sub_u
  drop
  )
    
  (func
  i32.const 0
  i32.const 0
  i32.atomic.rmw16.sub_u
  drop
  )
    
  (func
  i32.const 0
  i64.const 0
  i64.atomic.rmw8.sub_u
  drop
  )
    
  (func
  i32.const 0
  i64.const 0
  i64.atomic.rmw16.sub_u
  drop
  )
    
  (func
  i32.const 0
  i64.const 0
  i64.atomic.rmw32.sub_u
  drop
  )
    
  (func
  i32.const 0
  i32.const 0
  i32.atomic.rmw.and
  drop
  )
    
  (func
  i32.const 0
  i64.const 0
  i64.atomic.rmw.and
  drop
  )
    
  (func
  i32.const 0
  i32.const 0
  i32.atomic.rmw8.and_u
  drop
  )
    
  (func
  i32.const 0
  i32.const 0
  i32.atomic.rmw16.and_u
  drop
  )
    
  (func
  i32.const 0
  i64.const 0
  i64.atomic.rmw8.and_u
  drop
  )
    
  (func
  i32.const 0
  i64.const 0
  i64.atomic.rmw16.and_u
  drop
  )
    
  (func
  i32.const 0
  i64.const 0
  i64.atomic.rmw32.and_u
  drop
  )
    
  (func
  i32.const 0
  i32.const 0
  i32.atomic.rmw.or
  drop
  )
    
  (func
  i32.const 0
  i64.const 0
  i64.atomic.rmw.or
  drop
  )
    
  (func
  i32.const 0
  i32.const 0
  i32.atomic.rmw8.or_u
  drop
  )

  (func
  i32.const 0
  i32.const 0
  i32.atomic.rmw16.or_u
  drop
  )
  
  (func
  i32.const 0
  i64.const 0
  i64.atomic.rmw8.or_u
  drop
  )

  (func
  i32.const 0
  i64.const 0
  i64.atomic.rmw16.or_u
  drop
  )

  (func
  i32.const 0
  i64.const 0
  i64.atomic.rmw32.or_u
  drop
  )
 
  (func
  i32.const 0
  i32.const 0
  i32.atomic.rmw.xor
  drop
  )
 
  (func
  i32.const 0
  i64.const 0
  i64.atomic.rmw.xor
  drop
  )
 
  (func
  i32.const 0
  i32.const 0
  i32.atomic.rmw8.xor_u
  drop
  )
 
  (func
  i32.const 0
  i32.const 0
  i32.atomic.rmw16.xor_u
  drop
  )
 
  (func
  i32.const 0
  i64.const 0
  i64.atomic.rmw8.xor_u
  drop
  )
 
  (func
  i32.const 0
  i64.const 0
  i64.atomic.rmw16.xor_u
  drop
  )
 
  (func
  i32.const 0
  i64.const 0
  i64.atomic.rmw32.xor_u
  drop
  )
 
  (func
  i32.const 0
  i64.const 0
  i64.atomic.rmw.xchg
  drop
  )
 
  (func
  i32.const 0
  i32.const 0
  i32.atomic.rmw8.xchg_u
  drop
  )
 
  (func
  i32.const 0
  i32.const 0
  i32.atomic.rmw16.xchg_u
  drop
  )
 
  (func
  i32.const 0
  i64.const 0
  i64.atomic.rmw8.xchg_u
  drop
  )
 
  (func
  i32.const 0
  i64.const 0
  i64.atomic.rmw16.xchg_u
  drop
  )
 
  (func
  i32.const 0
  i64.const 0
  i64.atomic.rmw32.xchg_u
  drop
  )
 
  (func
  i32.const 0
  i64.const 0
  i64.const 0
  i64.atomic.rmw.cmpxchg
  drop
  )
 
  (func
  i32.const 0
  i32.const 0
  i32.const 0
  i32.atomic.rmw8.cmpxchg_u
  drop
  )
 
  (func
  i32.const 0
  i32.const 0
  i32.const 0
  i32.atomic.rmw16.cmpxchg_u
  drop
  )
 
  (func
  i32.const 0
  i64.const 0
  i64.const 0
  i64.atomic.rmw8.cmpxchg_u
  drop
  )
 
  (func
  i32.const 0
  i64.const 0
  i64.const 0
  i64.atomic.rmw16.cmpxchg_u
  drop
  )
 
  (func
  i32.const 0
  i64.const 0
  i64.const 0
  i64.atomic.rmw32.cmpxchg_u
  drop
  )
 
  (func
  i32.const 0
  i64.const 0
  i64.const 0
  memory.atomic.wait64
  drop
  )
 
  ;; Extended not implemented Wasm instructions (there much more than this).
  (func
    i32.const 0
    i32.const 0
    i32.const 0
    table.init 0
    elem.drop 0
    i32.const 0
    i32.const 0
    i32.const 0
    table.copy 0
    i32.const 0
    ref.func $func
    table.set $t_func
    i32.const 0
    table.get $t_func
    drop
    table.size $t_func
    drop
    i32.const 0
    v128.load
    drop
  )
)

