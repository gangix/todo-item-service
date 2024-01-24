package com.simple.system.simplesystemtask.domain.exception;
public class ItemNotFoundException extends RuntimeException {
   public ItemNotFoundException(Long id) {
      super(String.format("Item with id=%d not found", id));
   }

   public static ItemNotFoundException of(Long id) {
      return new ItemNotFoundException(id);
   }
}