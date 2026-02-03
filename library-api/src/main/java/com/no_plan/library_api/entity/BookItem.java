package com.no_plan.library_api.entity;

import com.no_plan.library_api.statusEnum.ItemStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "book_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BookItem {

    @Id
    @Column(name = "book_id")
    private String bookItemId;

    @ManyToOne
    @JoinColumn(name = "meta_id", nullable = false)
    private BookMeta bookMeta;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ItemStatus status;

    @Column(length = 50)
    private String location;

    @OneToMany(mappedBy = "bookItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Loan> loans = new ArrayList<>();

    public void changeStatus(ItemStatus status) {
        this.status = status;
    }

    public void changeLocation(String location) {
        this.location = location;
    }

}
