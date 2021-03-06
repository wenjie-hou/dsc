import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { DscSharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';
import { FooterComponent } from './components/footer/footer.component';
import { WrapperComponent } from './components/wrapper/wrapper.component';
import { SectionComponent } from './components/section/section.component';

@NgModule({
  imports: [DscSharedModule, RouterModule.forChild([HOME_ROUTE])],
  declarations: [HomeComponent, FooterComponent, WrapperComponent, SectionComponent]
})
export class DscHomeModule {}
