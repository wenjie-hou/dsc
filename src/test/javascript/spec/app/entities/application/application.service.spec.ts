import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { take, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { ApplicationService } from 'app/entities/application/application.service';
import { IApplication, Application } from 'app/shared/model/application.model';
import { State } from 'app/shared/model/enumerations/state.model';

describe('Service Tests', () => {
  describe('Application Service', () => {
    let injector: TestBed;
    let service: ApplicationService;
    let httpMock: HttpTestingController;
    let elemDefault: IApplication;
    let expectedResult;
    let currentDate: moment.Moment;
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule]
      });
      expectedResult = {};
      injector = getTestBed();
      service = injector.get(ApplicationService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new Application(0, State.CREATED, currentDate, currentDate);
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            end: currentDate.format(DATE_FORMAT),
            lastChanged: currentDate.format(DATE_FORMAT)
          },
          elemDefault
        );
        service
          .find(123)
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: elemDefault });
      });

      it('should create a Application', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            end: currentDate.format(DATE_FORMAT),
            lastChanged: currentDate.format(DATE_FORMAT)
          },
          elemDefault
        );
        const expected = Object.assign(
          {
            end: currentDate,
            lastChanged: currentDate
          },
          returnedFromService
        );
        service
          .create(new Application(null))
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));
        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: expected });
      });

      it('should update a Application', () => {
        const returnedFromService = Object.assign(
          {
            state: 'BBBBBB',
            end: currentDate.format(DATE_FORMAT),
            lastChanged: currentDate.format(DATE_FORMAT)
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            end: currentDate,
            lastChanged: currentDate
          },
          returnedFromService
        );
        service
          .update(expected)
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));
        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: expected });
      });

      it('should return a list of Application', () => {
        const returnedFromService = Object.assign(
          {
            state: 'BBBBBB',
            end: currentDate.format(DATE_FORMAT),
            lastChanged: currentDate.format(DATE_FORMAT)
          },
          elemDefault
        );
        const expected = Object.assign(
          {
            end: currentDate,
            lastChanged: currentDate
          },
          returnedFromService
        );
        service
          .query(expected)
          .pipe(
            take(1),
            map(resp => resp.body)
          )
          .subscribe(body => (expectedResult = body));
        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Application', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
